import { RouterProvider } from "react-router";
import { Router } from "./Router";
import { css, Global } from "@emotion/react";
import { ToastContainer } from "react-toastify";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Global
        styles={css`
          * {
            font-family: "Pretendard";
          }
        `}
      />
      <RouterProvider router={Router} />
      <ToastContainer />
    </QueryClientProvider>
  );
}

export default App;
