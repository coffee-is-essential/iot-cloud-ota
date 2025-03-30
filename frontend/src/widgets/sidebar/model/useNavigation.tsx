import { ReactNode, useMemo } from "react";
import {
  ChartColumn,
  LayoutDashboard,
  Megaphone,
  SquareTerminal,
} from "lucide-react";

export interface NavigationItemType {
  id: string;
  icon: ReactNode;
  name: string;
  path: string;
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
      },
      {
        id: "ads",
        icon: <Megaphone />,
        name: "광고 관리",
        path: "/ads",
      },
      {
        id: "monitoring",
        icon: <ChartColumn />,
        name: "모니터링",
        path: "/monitoring",
      },
    ],
    []
  );

  return {
    navigationItems,
  };
};
