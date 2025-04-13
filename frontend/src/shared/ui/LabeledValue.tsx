import { JSX } from "react";

/**
 * Interface for LabeledValue component props
 * @interface LabeledValueProps
 * @property {string} label - The label text to display
 * @property {string} value - The value text to display
 * @property {"sm" | "md" | "lg"} [size] - The size of the label and value text
 */
export interface LabeledValueProps {
  label: string;
  value: string | null;
  size?: "sm" | "md" | "lg";
}

const STYLES = {
  sm: {
    gap: "gap-2",
    label: "text-sm font-normal text-neutral-600",
    value: "text-base font-medium text-neutral-800",
  },
  md: {
    gap: "gap-2",
    label: "text-base font-normal text-neutral-600",
    value: "text-lg font-medium text-neutral-800",
  },
  lg: {
    gap: "gap-4",
    label: "text-lg font-normal text-neutral-600",
    value: "text-xl font-medium text-neutral-800",
  },
};

/**
 * A reusable component that displays a label and its corresponding value
 * @param {LabeledValueProps} props - The component props
 * @returns {JSX.Element} Rendered component
 */
export const LabeledValue = ({
  label,
  value,
  size = "sm",
}: LabeledValueProps): JSX.Element => {
  const {
    gap: gapStyles,
    label: labelStyles,
    value: valueStyles,
  } = STYLES[size];

  return (
    <div className={`flex flex-col ${gapStyles}`}>
      <div className={labelStyles}>{label}</div>
      <div className={valueStyles}>{value}</div>
    </div>
  );
};
