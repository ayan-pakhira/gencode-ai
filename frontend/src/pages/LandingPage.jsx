import React from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { FaGithub } from "react-icons/fa";
import { useAuth } from "../context/AuthContext.jsx";

function LandingPage() {

  
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  // If user is already authenticated, redirect to chat page
  if (isAuthenticated) {
    return <Navigate to="/chat" replace />;
  }

  const loginWithGithub = () => {
   window.location.href = `${import.meta.env.VITE_API_URL}/oauth2/authorization/github`;
  }
  
  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-b from-blue-700 via-indigo-800 to-purple-900">
      {/* Header */}
      <header className="w-full flex justify-between items-center px-12 py-6">
        <h1 className="text-2xl font-extrabold text-white tracking-wide">
          GenCode AI
        </h1>
      </header>

      {/* Hero Section */}
      <main className="flex flex-col justify-center items-center flex-grow text-center px-6">
        <p className="text-4xl sm:text-5xl md:text-6xl font-bold text-white/90 mb-6">
          Welcome to
        </p>

        <h2
          className="text-6xl sm:text-7xl md:text-8xl font-extrabold bg-clip-text text-transparent
          bg-gradient-to-r from-blue-300 via-purple-300 to-pink-400 drop-shadow-lg"
        >
          GenCode AI
        </h2>

        <p className="mt-6 max-w-2xl text-lg text-white/70">
          Empowering developers with AI-driven coding solutions. Fast,
          intelligent, and built for innovation.
        </p>

        {/* Action Buttons */}
        <div className="mt-12 flex flex-col sm:flex-row gap-6 w-full max-w-md justify-center">
          {/* <Link
            className="flex-1 px-6 py-5 rounded-full text-xl font-semibold text-white
                      bg-gradient-to-r from-blue-500 to-purple-600 shadow-lg
                      hover:scale-105 hover:shadow-xl transition duration-300 text-center"
          >
            Get Started
          </Link> */}

          <button
            onClick={loginWithGithub}
            className=" flex items-center justify-center gap-3 px-6 py-3 rounded-full
                       bg-white text-gray-700 font-semibold shadow-md
                       hover:bg-gray-100 hover:scale-105 transition duration-300"
          >
            <FaGithub size={22} />
            Continue with Github
          </button>
        </div>
      </main>

      {/* Footer */}
      <footer className="w-full py-4 text-center text-white/50 text-sm border-t border-white/10">
        © {new Date().getFullYear()} GenCode AI. All rights reserved.
      </footer>
    </div>
  );
}

export default LandingPage;
