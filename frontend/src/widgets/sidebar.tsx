import {
  ChartColumn,
  LayoutDashboard,
  Megaphone,
  SquareTerminal,
} from "lucide-react";

const SideBar = () => {
  return (
    <div className="flex flex-col h-full bg-slate-900 w-60">
      <div className="flex flex-col items-center justify-center h-1/4">
        <div className="flex items-center justify-center w-32 h-32 rounded-full bg-white/50">
          {/* TODO: Add User Avatar */}
        </div>
        {/* TODO: Get Username */}
        <p className="mt-4 text-gray-300 ">Junwoo Park</p>
      </div>
      <div className="flex justify-center py-6">
        <div className="w-2/3 h-px my-2 bg-gray-500"></div>
      </div>
      <div className="flex flex-col items-start flex-grow gap-12 px-16">
        <div className="flex items-center gap-3 text-gray-200">
          <LayoutDashboard size={20} />
          <p>대시보드</p>
        </div>
        <div className="flex items-center gap-3 text-gray-200">
          <SquareTerminal size={20} />
          <p>펌웨어 관리</p>
        </div>
        <div className="flex items-center gap-3 text-gray-200">
          <Megaphone size={20} />
          <p>광고 관리</p>
        </div>
        <div className="flex items-center gap-3 text-gray-200">
          <ChartColumn size={20} />
          <p>모니터링</p>
        </div>
      </div>
    </div>
  );
};

export default SideBar;
