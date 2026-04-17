import LandingPage from "./pages/LandingPage.jsx";
import ChatPage from "./pages/ChatPage.jsx";
import Layout from "./Layout.jsx";
import ProtectedRoutes from "./components/ProtectedRoutes.jsx";
import OAuthSuccess from "./components/OAuthSuccess.jsx";
import httpClient from "./config/AxiosHelper.js";

import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

function App() {
  return (
    <Router>
      <Routes>

        <Route path="/" element={<LandingPage />} />
        <Route path="/oauth-success" element={<OAuthSuccess />} />

        
        <Route
          element={
            <ProtectedRoutes>
              <Layout />
            </ProtectedRoutes>
          }
        >
          <Route path="/chat" element={<ChatPage />} />
        </Route>

        
        <Route path="*" element={<Navigate to="/" replace />} />

      </Routes>
    </Router>
  );
}

export default App;
