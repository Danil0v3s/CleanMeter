import * as React from "react"

type Theme = "dark" | "light"

type ThemeContextValue = {
  theme: Theme
  resolvedTheme: Theme
  setTheme: (theme: Theme) => void
  toggleTheme: () => void
}

const ThemeContext = React.createContext<ThemeContextValue | null>(null)

function applyThemeClass(theme: Theme) {
  const root = document.documentElement
  root.classList.toggle("dark", theme === "dark")
  root.classList.toggle("light", theme === "light")
}

function ThemeProvider({
  children,
  defaultTheme = "light",
  enableHotkey = true,
}: {
  children: React.ReactNode
  defaultTheme?: Theme
  enableHotkey?: boolean
}) {
  const [theme, setThemeState] = React.useState<Theme>(defaultTheme)

  React.useEffect(() => {
    applyThemeClass(theme)
  }, [theme])

  const setTheme = React.useCallback((next: Theme) => setThemeState(next), [])
  const toggleTheme = React.useCallback(
    () => setThemeState((t) => (t === "dark" ? "light" : "dark")),
    [],
  )

  React.useEffect(() => {
    if (!enableHotkey) return

    function onKeyDown(event: KeyboardEvent) {
      if (event.defaultPrevented || event.repeat) return
      if (event.metaKey || event.ctrlKey || event.altKey) return
      if (event.key.toLowerCase() !== "d") return
      if (isTypingTarget(event.target)) return
      toggleTheme()
    }

    window.addEventListener("keydown", onKeyDown)
    return () => window.removeEventListener("keydown", onKeyDown)
  }, [enableHotkey, toggleTheme])

  const value = React.useMemo<ThemeContextValue>(
    () => ({ theme, resolvedTheme: theme, setTheme, toggleTheme }),
    [theme, setTheme, toggleTheme],
  )

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
}

function useTheme() {
  const ctx = React.useContext(ThemeContext)
  if (!ctx) throw new Error("useTheme must be used within a ThemeProvider")
  return ctx
}

function isTypingTarget(target: EventTarget | null) {
  if (!(target instanceof HTMLElement)) return false
  return (
    target.isContentEditable ||
    target.tagName === "INPUT" ||
    target.tagName === "TEXTAREA" ||
    target.tagName === "SELECT"
  )
}

export { ThemeProvider, useTheme }
