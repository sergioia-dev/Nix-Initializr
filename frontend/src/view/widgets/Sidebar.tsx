import { useState } from "react";
import { NavLink } from "react-router-dom";
import "../style/Sidebar.css";
import {
  ChevronDouble,
  ChevronDown,
  HomeIcon,
  ToolsIcon,
  InfoIcon,
  LoginIcon,
} from "../../assets";

interface SidebarProps {
  children?: React.ReactNode;
}

export default function Sidebar({ children }: SidebarProps) {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [aboutSubmenuOpen, setAboutSubmenuOpen] = useState(false);

  const toggleSidebar = () => setSidebarOpen((prev) => !prev);
  const toggleSubMenu = () => setAboutSubmenuOpen((prev) => !prev);

  return (
    <nav id="sidebar" className={sidebarOpen ? "" : "close"}>
      <ul>
        <li>
          <span className="logo">Nix Docs</span>
          <button onClick={toggleSidebar} id="toggle-btn">
            <ChevronDouble />
          </button>
        </li>
        <li>
          <NavLink to="/" className={({ isActive }) => isActive ? "active" : ""}>
            <HomeIcon />
            <span>Home</span>
          </NavLink>
        </li>
        <li>
          <NavLink to="/nix-initializr" className={({ isActive }) => isActive ? "active" : ""}>
            <ToolsIcon />
            <span>Nix Initializr</span>
          </NavLink>
        </li>
        <li>
          <button
            onClick={toggleSubMenu}
            className={`dropdown-btn${aboutSubmenuOpen ? " rotate" : ""}`}
          >
            <InfoIcon />
            <span>About Nix Docs</span>
            <ChevronDown />
          </button>
          <ul className={`sub-menu${aboutSubmenuOpen ? " show" : ""}`}>
            <div />
          </ul>
        </li>
        {children}
        <li className="signInBtn">
          <NavLink to="/signin" className={({ isActive }) => isActive ? "active" : ""}>
            <LoginIcon />
            <span>Sign in</span>
          </NavLink>
        </li>
      </ul>
    </nav>
  );
}
