import { useNavigation } from "../model/useNavigation";
import { NavigationItem } from "./NavigationItem";

export const NavigationMenu = () => {
  const { navigationItems } = useNavigation();

  return (
    <div className="flex flex-col items-start gap-12 px-16">
      {navigationItems.map((item) => (
        <NavigationItem key={item.id} icon={item.icon} name={item.name} />
      ))}
    </div>
  );
};
