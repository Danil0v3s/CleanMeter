import React from "react"
import ReactDOM from "react-dom/client"
import "@/globals.css"
import { OverlayApp } from "@/overlay/OverlayApp"

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <OverlayApp />
  </React.StrictMode>,
)
