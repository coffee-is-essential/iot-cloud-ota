import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./app/App";

const start = async () => {
  const { worker } = await import("./shared/mocks/browsers");
  worker.start();

  createRoot(document.getElementById("root")!).render(<App />);
};

start();
