import { Outlet } from "react-router";
import { SideBar } from "../../sidebar/ui/Sidebar";

export const Layout = () => {
  return (
    <div className="flex h-screen">
      <SideBar />
      <main className="flex-1 p-4 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  );
};
