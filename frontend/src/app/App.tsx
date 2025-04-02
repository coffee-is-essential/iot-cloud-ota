import { RouterProvider } from "react-router";
import { Router } from "./Router";
import { css, Global } from "@emotion/react";

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
    </div>
  );
}

export default App;
