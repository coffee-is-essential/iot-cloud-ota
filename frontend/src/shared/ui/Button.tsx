import { JSX } from "react";

/**
 * Interface for Button component props
 */
export interface ButtonProps {
  title: string;
  icon?: JSX.Element;
  onClick?: () => void;
  disabled?: boolean;
  type?: "button" | "submit" | "reset";
  variant?: "primary" | "secondary";
}

/**
 * A reusable button component with consistent styling
 * @param {ButtonProps} props - The component props
 * @param {string} props.title - Text to display inside the button
 * @param {JSX.Element} [props.icon] - Optional icon to display inside the button
 * @param {Function} [props.onClick] - Optional click handler function
 * @param {boolean} [props.disabled] - Optional flag to disable the button
 * @param {string} [props.type] - Type of the button (default is "button")
 * @param {string} [props.variant] - Optional variant for button styling
 * @returns {JSX.Element} Rendered button component
 */
export const Button = ({
  title,
  icon,
  onClick,
  disabled,
  type = "button",
  variant = "primary",
}: ButtonProps): JSX.Element => {
  const disabledStyles = disabled
    ? "opacity-60 cursor-not-allowed"
    : "shadow-sm hover:shadow";

  const variantStyles = {
    primary: "bg-slate-900 text-white",
    secondary: "bg-gray-200 text-gray-700 hover:bg-gray-300",
  };

  return (
    <button
      className={`px-4 py-1.5 flex gap-2 items-center justify-center text-sm transition-all duration-200 rounded-md focus:ring-2 focus:ring-offset-2 ${variantStyles[variant]} ${disabledStyles}`}
      onClick={onClick}
      disabled={disabled}
      type={type}
    >
      {icon && <span>{icon}</span>}
      {title}
    </button>
  );
};
