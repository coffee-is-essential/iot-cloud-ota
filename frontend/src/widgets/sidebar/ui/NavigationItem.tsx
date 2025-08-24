import { JSX, ReactNode } from "react";

/**
 * 네비게이션 아이템 컴포넌트 props 인터페이스
 * @interface
 * @param {ReactNode} icon - 아이콘 컴포넌트 (선택 사항)
 * @param {string} name - 네비게이션 아이템 이름
 * @param {boolean} isActive - 현재 활성화된 아이템인지 여부
 * @param {boolean} isParent - 부모 아이템인지 여부 (자식 아이템이 있는 경우)
 * @param {boolean} isChild - 자식 아이템인지 여부
 * @param {Function} onClick - 아이템 클릭 시 호출되는 콜백 함수
 */
export interface NavigationItemProps {
  icon?: ReactNode;
  name: string;
  isActive?: boolean;
  isParent?: boolean;
  isChild?: boolean;
  onClick?: () => void;
}

/**
 * 네비게이션 아이템 컴포넌트입니다.
 * 사이드바 메뉴 & 드롭다운 메뉴에서 각 페이지를 표시하는 데 사용됩니다.
 * @param {NavigationItemProps} props - 컴포넌트 props
 * @returns {JSX.Element} 네비게이션 아이템 컴포넌트
 */
export const NavigationItem = ({
  icon,
  name,
  isActive = false,
  isParent = false,
  isChild = false,
  onClick = () => {},
}: NavigationItemProps): JSX.Element => {
  const baseClasses =
    "group relative flex items-center transition-all duration-200 ease-in-out rounded-lg";

  const sizeClasses = isChild ? "px-3 py-2 text-sm" : "px-4 py-3";

  const colorClasses = isActive
    ? isChild
      ? "bg-slate-700/60 text-slate-200 shadow-sm"
      : "bg-slate-700/50 text-slate-100 shadow-lg shadow-slate-900/20"
    : isChild
      ? "text-gray-400 hover:text-gray-200 hover:bg-gray-800/50"
      : "text-gray-300 hover:text-white hover:bg-gray-800/50";

  return (
    <div
      className={`${baseClasses} ${sizeClasses} ${colorClasses} cursor-pointer`}
      onClick={onClick}
    >
      {/* 활성화 여부 표시 */}
      {isActive && !isChild && (
        <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-slate-400 rounded-r-full" />
      )}

      {icon && (
        <div
          className={`flex-shrink-0 transition-transform duration-200 ${isActive ? "scale-110" : "group-hover:scale-105"} ${isChild ? "mr-2" : "mr-3"}`}
        >
          {icon}
        </div>
      )}

      <span
        className={`font-medium ${isChild ? "text-sm" : "text-base"} ${isActive && !isChild ? "font-semibold" : ""}`}
      >
        {name}
      </span>

      {isActive && isParent && (
        <div className="absolute inset-0 bg-slate-800/30 rounded-lg -z-10" />
      )}
    </div>
  );
};
