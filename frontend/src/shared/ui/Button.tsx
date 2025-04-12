import { JSX } from "react";

/**
 * Interface for Button component props
 */
export interface ButtonProps {
  icon?: JSX.Element;
  title: string;
  onClick?: () => void;
  disabled?: boolean;
}

/**
 * A reusable button component with consistent styling
 * @param {ButtonProps} props - The component props
 * @param {JSX.Element} [props.icon] - Optional icon to display inside the button
 * @param {string} props.title - Text to dispaly inside the button
 * @param {Function} [props.onClick] - Optional click handler function
 * @param {boolean} [props.disabled] - Optional flag to disable the button
 * @returns {JSX.Element} Rendered button component
 */
export const Button = ({
  icon,
  title,
  onClick,
  disabled,
}: ButtonProps): JSX.Element => {
  const disabledStyles = disabled
    ? "opacity-60 cursor-not-allowed"
    : "shadow-sm hover:shadow";

  return (
    <button
      className={`px-4 py-1.5 flex gap-2 items-center justify-center text-sm text-white transition-all duration-200 rounded-md bg-slate-900 focus:ring-2 focus:ring-offset-2 ${disabledStyles}`}
      onClick={onClick}
    >
      {icon && <span>{icon}</span>}
      {title}
    </button>
  );
};
