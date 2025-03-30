import { RouterProvider } from "react-router";
import { Router } from "./Router";
import { css, Global } from "@emotion/react";
import { fontFaces } from "../shared/styles/fonts";

function App() {
  return (
    <div>
      <Global
        styles={css`
          ${fontFaces}
          * {
            font-family: "NanumSquareRound";
          }
        `}
      />
      <RouterProvider router={Router} />
    </div>
  );
}

export default App;
