import React from "react";

import EVHeader from "./layout/Header";
import { EVFooter } from "./layout/Footer";

export default function Homepage() {
  return (
    <>
      {/* Header trên cùng */}
      <EVHeader />

      {/* Footer dưới cùng */}
      <EVFooter />
    </>
  );
}
