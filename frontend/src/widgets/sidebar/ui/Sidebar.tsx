import Divider from "@mui/material/Divider";
import { NavigationMenu } from "./NavigationMenu";

export const SideBar = () => {
  return (
    <div className="flex flex-col h-full bg-slate-900 w-60">
      <div className="flex flex-col items-center justify-center mt-8">
        <div className="flex items-center justify-center w-32 h-32 rounded-full bg-white/50">
          {/* TODO: Add User Avatar */}
        </div>
        {/* TODO: Get Username */}
        <p className="mt-4 text-gray-300 ">Junwoo Park</p>
      </div>
      <Divider
        sx={{
          bgcolor: "#505050",
          width: "66%",
          margin: "0 auto",
          my: 6,
        }}
      />
      <NavigationMenu />
    </div>
  );
};
