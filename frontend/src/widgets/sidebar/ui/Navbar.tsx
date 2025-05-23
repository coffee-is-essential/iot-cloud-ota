import { useState } from "react";
import { useMediaQuery } from "@mui/material";
import { Menu } from "lucide-react";
import { NavigationMenu } from "./NavigationMenu";

export const Navbar = () => {
  const isMobile = useMediaQuery("(max-width: 1024px)"); // Hook for mobile view
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  if (!isMobile) { // Hide navbar on desktop
    return null;
  }

  return (
    <nav className="fixed top-0 left-0 right-0 bg-slate-900 z-50">
      <div className="flex items-center justify-between px-4 py-3">
        <div className="flex items-center gap-2">
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="text-gray-300 hover:text-white"
          >
            <Menu size={24} />
          </button>
          <p className="text-gray-300">Junwoo Park</p>
        </div>
      </div>
      
      {/* 모바일 메뉴 */}
      {isMenuOpen && (
        <div className="bg-slate-800 py-4">
          <NavigationMenu onMenuClick={() => setIsMenuOpen(false)} />
        </div>
      )}
    </nav>
  );
}; 