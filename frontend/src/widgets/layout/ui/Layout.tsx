import { Outlet } from "react-router";
import { SideBar } from "../../sidebar/ui/Sidebar";
import { Navbar } from "../../sidebar/ui/Navbar";
import { useMediaQuery } from "@mui/material";

export const Layout = () => {
  const isMobile = useMediaQuery("(max-width: 1024px)");

  return (
    <div className="flex h-screen">
      <Navbar />
      <SideBar />
      {/* mt-12 is for mobile view to avoid overlap with navbar */}
      {/* ml-60 is for desktop view to avoid overlap with sidebar */}
      <main className={`flex-1 p-8 overflow-y-auto bg-slate-200 ${isMobile ? 'mt-12' : 'ml-60'}`}>
        <Outlet />
      </main>
    </div>
  );
};
