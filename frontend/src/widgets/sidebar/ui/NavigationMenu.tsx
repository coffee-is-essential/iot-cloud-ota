import { Link, useLocation } from "react-router";
import { useNavigation } from "../model/useNavigation";
import { NavigationItem } from "./NavigationItem";

export const NavigationMenu = () => {
  const { navigationItems } = useNavigation();

  // 현재 경로 계산, NavigationItem이 Active한지 판별하는데 사용
  const location = useLocation();
  const currentPath = location.pathname;

  return (
    <div className="flex flex-col items-start gap-12 pl-16">
      {navigationItems.map((item) => (
        <Link to={item.path} key={item.id}>
          <NavigationItem
            key={item.id}
            icon={item.icon}
            name={item.name}
            isActive={currentPath.startsWith(item.path)}
          />
        </Link>
      ))}
    </div>
  );
};
