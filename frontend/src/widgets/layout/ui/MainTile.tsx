import { ReactNode } from "react";

export interface MainTileProps {
  title: string;
  rightElement?: ReactNode;
  children?: ReactNode;
}

export const MainTile = ({ title, rightElement, children }: MainTileProps) => {
  return (
    <div className="flex flex-col p-8 bg-white rounded-md">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-base text-neutral-600">{title}</h2>
        {rightElement}
      </div>
      <div className="flex-1">{children}</div>
    </div>
  );
};
