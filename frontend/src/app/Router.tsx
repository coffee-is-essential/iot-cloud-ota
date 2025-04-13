import { createBrowserRouter, Navigate } from "react-router";
import { Layout } from "../widgets/layout/ui/Layout";
import { Dashboard } from "../pages/dashboard";
import { FirmwarePage } from "../pages/firmware";
import { Advertisement } from "../pages/advertisement";
import { Monitoring } from "../pages/monitoring";
import { FirmwareDetailPage } from "../pages/FirmwareDetailPage";

export const Router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      // 초기 엔트리 경로를 dashboard로 지정
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <Dashboard /> },
      { path: "firmware", element: <FirmwarePage /> },
      { path: "firmware/:id", element: <FirmwareDetailPage /> },
      { path: "ads", element: <Advertisement /> },
      { path: "monitoring", element: <Monitoring /> },
    ],
  },
]);
