import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function OAuthSuccess() {
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const token = params.get("token");

        if (token) {
            login(token);
            navigate("/chat", { replace: true });
        } else {
            navigate("/", { replace: true });
        }
    }, [navigate, login]);

    return <p>Logging you in…</p>;
}

export default OAuthSuccess;
