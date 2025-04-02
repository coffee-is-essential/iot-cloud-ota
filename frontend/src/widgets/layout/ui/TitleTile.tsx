export interface TitleTileProps {
  title: string;
  description: string;
}

export const TitleTile = ({ title, description }: TitleTileProps) => {
  return (
    <div className="flex flex-col gap-2 p-8 bg-white rounded-lg text-neutral-600">
      <p className="text-2xl font-medium">{title}</p>
      <p className="text-sm">{description}</p>
    </div>
  );
};
