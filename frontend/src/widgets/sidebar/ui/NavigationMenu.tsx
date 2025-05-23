import { Link, useLocation } from "react-router";
import { useNavigation } from "../model/useNavigation";
import { NavigationItem } from "./NavigationItem";

interface NavigationMenuProps {
  onMenuClick?: () => void;
}

export const NavigationMenu = ({ onMenuClick }: NavigationMenuProps) => {
  const { navigationItems } = useNavigation();

  // Calculate current path, used to determine if NavigationItem is active
  const location = useLocation();
  const currentPath = location.pathname;

  return (
    <div className="flex flex-col items-start gap-12 pl-16">
      {navigationItems.map((item) => (
        <Link to={item.path} key={item.id} onClick={onMenuClick}>
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
