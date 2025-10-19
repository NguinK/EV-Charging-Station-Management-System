/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        ev: {
          background: "#050b0a",
          surface: "#101418",
          accent: "#2cd06d",
          accentLight: "#49eb85",
        },
      },
    },
  },
  plugins: [],
};
