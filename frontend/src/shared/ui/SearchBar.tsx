import { Search } from "lucide-react";
import React, { useState } from "react";

export interface SearchBarProps {
  placeholder?: string;
  onSearch: (query: string) => void;
}

export const SearchBar = ({
  placeholder = "검색",
  onSearch,
}: SearchBarProps) => {
  const [searchValue, setSearchValue] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchValue(value);
    onSearch(value);
  };

  return (
    <div className="flex items-center gap-4 py-2 pl-4 rounded-md bg-zinc-50">
      <Search className="w-4 text-neutral-600" />
      <input
        type="text"
        value={searchValue}
        onChange={handleChange}
        placeholder={placeholder}
        className="text-sm rounded-md bg-zinc-50 text-neutral-600 focus:outline-none"
      />
    </div>
  );
};
