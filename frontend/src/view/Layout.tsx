import { Outlet } from "react-router-dom";
import Sidebar from "./widgets/Sidebar";
import { MoonIcon, SunIcon, LanguageIcon } from "../assets";

interface LayoutProps {
  toggleTheme: () => void;
}

export default function Layout({ toggleTheme }: LayoutProps) {
  return (
    <>
      <Sidebar />
      <div>
        <header>
          <button onClick={toggleTheme} id="theme-switch">
            <MoonIcon />
            <SunIcon />
          </button>
          <button id="language" name="english">
            <a href="" id="language_link">
              <LanguageIcon />
            </a>
          </button>
        </header>
        <main>
          <Outlet />
        </main>
      </div>
    </>
  );
}
