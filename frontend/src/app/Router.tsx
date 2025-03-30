import { createBrowserRouter, Navigate } from "react-router";
import { Layout } from "../widgets/layout/ui/Layout";
import { Dashboard } from "../pages/dashboard";
import { Firmware } from "../pages/firmware";
import { Advertisement } from "../pages/advertisement";
import { Monitoring } from "../pages/monitoring";

export const Router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      // 초기 엔트리 경로를 dashboard로 지정
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <Dashboard /> },
      { path: "firmware", element: <Firmware /> },
      { path: "ads", element: <Advertisement /> },
      { path: "monitoring", element: <Monitoring /> },
    ],
  },
]);
