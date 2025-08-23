import { RouterProvider } from "react-router";
import { Router } from "./Router";
import { css, Global } from "@emotion/react";
import { ToastContainer } from "react-toastify";

function App() {
  return (
    <div>
      <Global
        styles={css`
          * {
            font-family: "Pretendard";
          }
        `}
      />
      <RouterProvider router={Router} />
      <ToastContainer />
    </div>
  );
}

export default App;
