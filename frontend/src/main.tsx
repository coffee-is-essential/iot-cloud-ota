import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./app/App";

const start = async () => {
  createRoot(document.getElementById("root")!).render(<App />);
};

start();
