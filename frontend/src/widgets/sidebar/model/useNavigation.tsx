import { ReactNode, useMemo } from "react";
import {
  Bot,
  ChartColumn,
  LayoutDashboard,
  Megaphone,
  SquareTerminal,
} from "lucide-react";

export interface NavigationItemType {
  id: string;
  icon?: ReactNode;
  name: string;
  path: string;
  children?: NavigationItemType[];
}

export const useNavigation = () => {
  const navigationItems = useMemo<NavigationItemType[]>(
    () => [
      {
        id: "dashboard",
        icon: <LayoutDashboard />,
        name: "대시보드",
        path: "/dashboard",
      },
      {
        id: "firmware",
        icon: <SquareTerminal />,
        name: "펌웨어 관리",
        path: "/firmware",
        children: [
          {
            id: "firmware-list",
            name: "펌웨어 목록",
            path: "/firmware/list",
          },
          {
            id: "firmware-deployment",
            name: "배포 관리",
            path: "/firmware/deployment",
          },
        ],
      },
      {
        id: "device",
        icon: <Bot />,
        name: "기기 관리",
        path: "/device",
      },
      {
        id: "ads",
        icon: <Megaphone />,
        name: "광고 관리",
        path: "/ads",
        children: [
          {
            id: "ads-list",
            name: "광고 목록",
            path: "/ads/list",
          },
          {
            id: "ads-deployment",
            name: "배포 관리",
            path: "/ads/deployment",
          },
        ],
      },
      {
        id: "monitoring",
        icon: <ChartColumn />,
        name: "모니터링",
        path: "/monitoring",
      },
    ],
    [],
  );

  return {
    navigationItems,
  };
};
