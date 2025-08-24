import { Link, useLocation } from "react-router";
import { useNavigation } from "../model/useNavigation";
import { NavigationItem } from "./NavigationItem";

interface NavigationMenuProps {
  onMenuClick?: () => void;
}

export const NavigationMenu = ({ onMenuClick }: NavigationMenuProps) => {
  const { navigationItems } = useNavigation();
  const location = useLocation();
  const currentPath = location.pathname;

  return (
    <nav className="flex flex-col gap-2 p-6">
      {navigationItems.map((item) => {
        const isParentActive = currentPath.startsWith(item.path);

        return (
          <div key={item.id} className="mb-2">
            <Link to={item.path} onClick={onMenuClick} className="block">
              <NavigationItem
                icon={item.icon}
                name={item.name}
                isActive={currentPath.startsWith(item.path)}
                isParent={!!item.children}
              />
            </Link>

            {isParentActive && item.children && (
              <div className="ml-3 mt-4 space-y-2 border-l-2 border-gray-700/50 pl-4">
                {item.children.map((child) => (
                  <Link
                    to={child.path}
                    key={child.id}
                    onClick={onMenuClick}
                    className="block"
                  >
                    <NavigationItem
                      name={child.name}
                      isActive={currentPath === child.path}
                      isChild={true}
                    />
                  </Link>
                ))}
              </div>
            )}
          </div>
        );
      })}
    </nav>
  );
};
