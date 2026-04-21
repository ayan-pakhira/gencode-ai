import { useState, useRef, useEffect } from "react";
import { Menu, X, Send, Image, MoreHorizontal, Trash2 } from "lucide-react";
import {
  createChat,
  fetchChatList,
  deleteChat,
  fetchMessages,
  processAI,
  getStatus,
} from "../services/UserServices.js";
import { useAuth } from "../context/AuthContext.jsx";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { setToken, getToken } from "../services/TokenService.js";
import MessageRenderer from "../components/MessageRenderer.jsx";
import {
  connectWebSocket,
  disconnectWebSocket,
} from "../services/WebSocketService.js";

function ChatPage() {
  const [isOpen, setIsOpen] = useState(false);
  const [message, setMessage] = useState("");

  const USER_COLOR = "bg-[#1f2937]";

  const fileInputRef = useRef(null);
  const messagesEndRef = useRef(null);
  const textareaRef = useRef(null);

  const [search, setSearch] = useState("");
  const [chats, setChats] = useState([]);
  const [activeChatId, setActiveChatId] = useState(null);

  const [menuOpenId, setMenuOpenId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [selectedImage, setSelectedImage] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [newChatName, setNewChatName] = useState("");
  const [loading, setLoading] = useState(false);

  //for status tracking
  const [statusMap, setStatusMap] = useState({});

  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const filteredChats = chats.filter((chat) =>
    chat.chatName?.toLowerCase().includes(search.toLowerCase()),
  );

  //logout function
  const handleLogOut = () => {
    logout();
    navigate("/", { replace: true });
  };

  //fetch chats
  const loadChats = async () => {
    try {
      if (!isAuthenticated) return;

      //console.log("Loading chats with token:", getToken());
      const chatList = await fetchChatList();
      //console.log("Fetched chats:", chatList);

      if (chatList) {
        setChats(chatList);
      } else {
        setChats([]);
      }
    } catch (error) {
      //console.log("Error loading chats:", error);
      throw error;
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      loadChats();
    }
  }, [isAuthenticated]);

  //load messages when active chat changes
  useEffect(() => {
    const loadMessages = async () => {
      if (!activeChatId) return;

      try {
        const res = await fetchMessages(activeChatId);
        setMessages(res);
      } catch (error) {
        //console.log("Error loading messages:", error);
        throw error;
      }
    };
    loadMessages();
  }, [activeChatId]);

  //delete chat
  const handleDeleteChat = async () => {
    try {
      const response = await deleteChat(activeChatId);
      //console.log("Chat deleted:", response);
      setActiveChatId(null);
      setMessages([]);
      loadChats();
    } catch (error) {
      //console.log("Error deleting chat:", error);
      throw error;
      toast.error("Failed to delete chat. Please try again.");
    }
  };

  //create chat
  const handleCreateChat = async () => {
    setActiveChatId(null);
    setMessages([]);
    setShowModal(false);
  };

  //generating chat name prompts
  const generateChatName = (prompt) => {
    return prompt.split(" ").slice(0, 5).join(" ");
  };

  //sending message
  const handleSend = async () => {
    if (!message.trim() && !selectedImage) return;

    let chatId = activeChatId;

    if (!chatId) {
      const chatName = generateChatName(message);
      try {
        const newChat = await createChat(chatName);
        chatId = newChat.chatId;
        setActiveChatId(chatId);

        await loadChats();
      } catch (error) {
       // console.error("Error creating chat:", error);
        toast.error("Failed to create chat. Please try again.");
        return;
      }
    }

    const finalChatId = chatId;

    const tempId = Date.now();
    const tempUserMessage = {
      id: tempId,
      sender: "USER",
      content: message,
      chatId: finalChatId,
      imageUrl: selectedImage ? selectedImage.previewUrl : null,
    };
    setMessages((prev) => [...prev, tempUserMessage]);

    const currentMessage = message;
    const imageToSend = selectedImage;

    setMessage("");
    setSelectedImage(null);
    autoResize();

    try {
      setLoading(true);
      const response = await processAI({
        chatId: finalChatId,
        prompt: currentMessage,
        image: imageToSend?.file,
      });

      const { userMessage, messageId } = response;

      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === tempId ? { ...userMessage, id: messageId } : msg,
        ),
      );

      setStatusMap((prev) => ({ ...prev, [messageId]: "PENDING" }));
    } catch (error) {
      console.error("AI error:", error);

      if (error.response?.status === 429) {
        setMessages((prev) => [
          ...prev,
          {
            id: Date.now(),
            sender: "AI",
            content: "⚠Try later.",
          },
        ]);
      } else {
        setMessages((prev) => [
          ...prev,
          {
            id: Date.now(),
            sender: "AI",
            content: "⚠ Something went wrong.",
          },
        ]);
      }
    } finally {
      setLoading(false);
    }
  };

  //websocket connection for real-time status updates
  useEffect(() => {
    connectWebSocket((data) => {
      //console.log("WebSocket message received:", data);

      const { chatId, messageId, status } = data;

      setStatusMap((prev) => ({ ...prev, [messageId]: status }));

      if (status === "COMPLETED") {
        fetchMessages(chatId).then((data) => {
          if (!Array.isArray(data)) return;

          setMessages((prev) => {
            if (prev.length === 0 && data.length > 0) {
              return data;
            }
            return data;
          });
        });
      }
    });

    return () => {
      disconnectWebSocket();
    };
  }, []);

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  /*text area auto resize*/
  const autoResize = () => {
    const el = textareaRef.current;
    if (!el) return;

    el.style.height = "auto";

    const maxHeight = 160;

    if (el.scrollHeight > maxHeight) {
      el.style.height = maxHeight + "px";
      el.style.overflowY = "auto";
    } else {
      el.style.height = el.scrollHeight + "px";
      el.style.overflowY = "hidden";
    }
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const previewUrl = URL.createObjectURL(file);

    setSelectedImage({file, previewUrl});
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    const handleClickOutside = () => setMenuOpenId(null);
    document.addEventListener("click", handleClickOutside);
    return () => document.removeEventListener("click", handleClickOutside);
  }, []);

  return (
    <div className="flex h-screen text-gray-200 bg-[#111827]">
      <button
        className="md:hidden fixed top-4 left-4 z-50 p-2 bg-[#0b0f14] rounded-lg"
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? <X /> : <Menu />}
      </button>

      <aside
        className={`fixed md:static top-0 left-0 h-full w-64 bg-[#0b0f14] border-r border-white/10 p-4 transform
        ${isOpen ? "translate-x-0" : "-translate-x-full"}
        md:translate-x-0 transition-transform duration-300`}
      >
        <h1 className="text-lg font-semibold mb-6">GenCode AI</h1>

        <button
          onClick={() => {
            setActiveChatId(null);
            setMessages([]);
            setStatusMap({});
            setShowModal(false);
          }}
          className="w-full mb-4 py-2 border border-white/10 rounded-lg hover:bg-white/5"
        >
          + New Chat
        </button>

        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search chats"
          className="w-full mb-6 px-3 py-2 rounded-md bg-white/5 placeholder-gray-400 outline-none"
        />

        <p className="text-xs text-gray-400 mb-2 uppercase tracking-wide">
          Your Chats
        </p>

        <div className="space-y-1 text-sm">
          {filteredChats.map((chat) => (
            <div
              key={chat.chatId}
              className={`relative p-2 rounded-md cursor-pointer transition group
                ${
                  activeChatId === chat.chatId
                    ? "bg-white/10 border border-white/10"
                    : "hover:bg-white/5"
                }`}
              onMouseLeave={() => setMenuOpenId(null)}
              onClick={() => setActiveChatId(chat.chatId)}
            >
              <div className="flex justify-between items-center">
                <span>{chat.chatName}</span>

                <div className="relative">
                  <MoreHorizontal
                    size={18}
                    className="opacity-0 group-hover:opacity-100 transition"
                    onClick={(e) => {
                      e.stopPropagation();
                      setMenuOpenId(
                        menuOpenId === chat.chatId ? null : chat.chatId,
                      );
                    }}
                  />

                  {menuOpenId === chat.chatId && (
                    <div className="absolute right-0 top-6 bg-[#1f2937] border border-white/20 rounded-md shadow-lg w-28 z-10">
                      <button
                        className="flex items-center gap-2 px-3 py-2 text-sm hover:bg-white/10 w-full"
                        onClick={handleDeleteChat}
                      >
                        <Trash2 size={16} />
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>

        <button
          onClick={handleLogOut}
          className="bg-red-950 text-slate-50 rounded absolute bottom-10 right-4 px-3 py-1 text-sm hover:bg-red-700 transition"
        >
          Logout
        </button>
      </aside>

      <main className="flex-1 flex flex-col">
        <header className="p-4 border-b border-white/10 font-medium">
          {chats.find((c) => c.chatId === activeChatId)?.chatName || "New Chat"}
        </header>

        <div className="flex-1 overflow-y-auto px-6 py-8 space-y-8">
          {!activeChatId ? (
            // EMPTY STATE UI
            <div className="flex flex-col items-center justify-center h-full text-center">
              <h1 className="text-2xl font-semibold mb-4">
                What can I help with?
              </h1>

              <p className="text-gray-400 mb-6">
                Start a new conversation by typing below
              </p>
            </div>
          ) : (
            // EXISTING CHAT UI
            <>
              {messages.map((msg, index) => {
                if (!msg) return null;

                const sender = (msg.sender || msg.senderId || "").toUpperCase();
                const isAI = sender === "AI";
                const isUser = sender === "USER";
                const status = statusMap[msg.id];

                return (
                  <div
                    key={msg.id || index}
                    className={`flex ${isAI ? "justify-start" : "justify-end"}`}
                  >
                    <div
                      className={`max-w-xl px-4 py-3 rounded-xl text-sm shadow whitespace-pre-wrap break-words
                        ${
                          isAI
                            ? "bg-[#0f172a] text-gray-200 border border-white/10"
                            : "bg-[#1f2937] text-white"
                        }`}
                    >
                      {msg.imageUrl && (
                        <img
                          src={msg.imageUrl}
                          alt="uploaded"
                          className="max-h-60 mb-2 rounded-lg border border-white/10"
                        />
                      )}

                      <MessageRenderer content={msg.content} />

                      {!isAI && status === "PENDING" && (
                        <div className="text-gray-400 text-xs mt-2">
                          Pending...
                        </div>
                      )}

                      {!isAI && status === "PROCESSING" && (
                        <div className="mt-2">
                          {msg.imageUrl && (
                            <div className="text-xs text-gray-400 mb-1">
                              🖼 Processing image...
                            </div>
                          )}
                          <div className="text-yellow-400 text-xs">
                            Processing...
                          </div>
                        </div>
                      )}

                      {!isAI && status === "FAILED" && (
                        <div className="text-red-400 text-xs mt-2">Failed</div>
                      )}
                    </div>
                  </div>
                );
              })}

              <div ref={messagesEndRef} />
            </>
          )}
        </div>

        {selectedImage && (
          <div className="px-6 pb-2">
            <div className="relative w-fit">
              <img
                src={selectedImage.previewUrl}
                alt="preview"
                className="max-h-40 rounded-lg border border-white/10"
              />
              <button
                onClick={() => setSelectedImage(null)}
                className="absolute top-1 right-1 bg-black/70 text-white text-xs px-2 py-1 rounded"
              >
                ✕
              </button>
            </div>
          </div>
        )}

        <div className="border-t border-white/10 p-4">
          <div className="flex max-w-3xl mx-auto gap-2 items-end">
            <input
              type="file"
              accept="image/*"
              ref={fileInputRef}
              onChange={handleImageUpload}
              className="hidden"
            />

            <button
              onClick={() => fileInputRef.current.click()}
              className="p-3 rounded-xl bg-[#0b0f14] border border-white/10 hover:bg-white/5"
            >
              <Image size={18} />
            </button>

            <textarea
              ref={textareaRef}
              rows={1}
              placeholder="Send a prompt..."
              value={message}
              onChange={(e) => {
                setMessage(e.target.value);
                autoResize();
              }}
              onKeyDown={handleKeyDown}
              className="flex-1 resize-none px-4 py-3 rounded-xl bg-[#0b0f14] border border-white/10 outline-none focus:ring-2 focus:ring-white/20 max-h-40 overflow-y-hidden scrollbar-dark"
            />

            <button
              onClick={handleSend}
              className="p-3 rounded-xl bg-white text-black hover:bg-gray-200"
            >
              <Send size={18} />
            </button>
          </div>
        </div>
      </main>

      {showModal && (
        <div
          className="fixed inset-0 bg-black/60 flex items-center justify-center z-50"
          onClick={() => setShowModal(false)}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="bg-[#0b0f14] p-6 rounded-xl w-80 border border-white/10"
          >
            <h2 className="text-lg font-semibold mb-4">Create New Chat</h2>

            <input
              autoFocus
              placeholder="Chat name"
              value={newChatName}
              onChange={(e) => setNewChatName(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleCreateChat()}
              className="w-full px-3 py-2 rounded-md bg-white/5 outline-none mb-4"
            />

            <button
              onClick={handleCreateChat}
              className="w-full py-2 rounded-lg bg-white text-black font-medium hover:bg-gray-200"
            >
              Create
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default ChatPage;
