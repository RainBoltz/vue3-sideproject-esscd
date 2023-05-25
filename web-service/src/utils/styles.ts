import Colors from '@/styles/main'

export const normalNodeStyle = {
  color: Colors.default.BUTTON_COLOR + "88",
  radius: 23,
  labelColor: "#000000ff",
};

export const normalEdgeStyle = {
  width: 1,
  color: Colors.PRIMARY_COLORS[8],
  dasharray: "0",
  linecap: "butt",
  animate: false,
  animationSpeed: 50,
};

export const highlightedNodeStyle = {
  color: Colors.default.BUTTON_COLOR + "ff",
  radius: 23,
  labelColor: "#000000ff",
};

export const highlightedEdgeStyle = {
  width: 3,
  color: Colors.default.WARNING_COLOR,
  dasharray: "6",
  linecap: "round",
  animate: true,
  animationSpeed: 50,
};

export const otherNodeStyle = {
  labelColor: "#00000055",
  color: Colors.default.BUTTON_COLOR + "33",
  radius: 23,
};

export const otherEdgeStyle = {
  width: 1,
  color: Colors.default.BUTTON_COLOR + "44",
  dasharray: "0",
  linecap: "butt",
  animate: false,
  animationSpeed: 50,
};
