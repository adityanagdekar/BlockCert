import "../style/ContentContainer.css";
const ContentContainer = ({ children }) => {
  // To render nested content
  return <div className="Content-Container">{children}</div>;
};

export default ContentContainer;
