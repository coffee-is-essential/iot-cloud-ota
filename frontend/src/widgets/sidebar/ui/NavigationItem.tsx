import { ReactNode } from "react";

interface NavigationItemProps {
  icon: ReactNode;
  name: string;
  isActive?: boolean;
  onClick?: () => void;
}

export const NavigationItem = ({
  icon,
  name,
  isActive = false,
  onClick = () => {},
}: NavigationItemProps) => {
  return (
    <div
      className={`flex items-center gap-3 text-gray-300 transition-colors cursor-pointer hover:text-white ${
        isActive ? "text-white" : ""
      }`}
      onClick={onClick}
    >
      {icon}
      <p>{name}</p>
    </div>
  );
};
