import "../style/MainContainer.css";
const MainContainer = ({ children }) => {
  // To render nested content
  return <div className="Main-Container">{children}</div>;
};

export default MainContainer;
