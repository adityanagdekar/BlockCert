import "../style/Button.css";

const Button = ({ text, onClick, type = "button", disabled = false }) => {
  return (
    <button
      className="Reusable-Button"
      onClick={onClick}
      type={type}
      disabled={disabled}
    >
      {text}
    </button>
  );
};

export default Button;
