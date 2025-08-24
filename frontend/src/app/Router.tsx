import { createBrowserRouter, Navigate } from "react-router";
import { Layout } from "../widgets/layout/ui/Layout";
import { DashboardPage } from "../pages/DashboardPage";
import { FirmwareListPage } from "../pages/FirmwareListPage";
import { AdsPage } from "../pages/AdsPage";
import { MonitoringPage } from "../pages/MonitoringPage";
import { FirmwareDetailPage } from "../pages/FirmwareDetailPage";
import { DevicePage } from "../pages/DevicePage";
import { FirmwareDeploymentPage } from "../pages/FirmwareDeploymentPage";

export const Router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      // 초기 엔트리 경로를 dashboard로 지정
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <DashboardPage /> },
      { path: "firmware", element: <Navigate to="/firmware/list" replace /> },
      { path: "firmware/list", element: <FirmwareListPage /> },
      { path: "firmware/:id", element: <FirmwareDetailPage /> },
      { path: "firmware/deployment", element: <FirmwareDeploymentPage /> },
      { path: "device", element: <DevicePage /> },
      { path: "ads", element: <AdsPage /> },
      { path: "monitoring", element: <MonitoringPage /> },
    ],
  },
]);
