import React, { useEffect, useState } from "react";

import EVHeader from "./layout/Header";
import { EVFooter } from "./layout/Footer";

export default function Homepage({ bgUrl }) {
  const plugs = ["Thu Duc City", "District 7", "District 3 "];
  const [idx, setIdx] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setIdx((prev) => (prev + 1) % plugs.length);
    }, 1500);
    return () => clearInterval(interval);
  }, [plugs]);
  return (
    <>
    <div className="flex flex-col min-h-screen bg-[#0b0f13]">
      {/* Header trên cùng */}
      <EVHeader />
      <section className="hero-root flex-grow">
      {/* CSS inline trong component */}
      <style>{`
        .hero-root { position: relative; width: 100%; min-height: 66vh; display:flex; align-items:center; justify-content:center; overflow:hidden;}
        .hero-bg { position:absolute; inset:0; background:#0b0f13 center/cover no-repeat; filter:saturate(0.9) brightness(0.8); }
        .hero-overlay { position:absolute; inset:0; background: radial-gradient(1200px 600px at 50% 60%, rgba(16, 185, 129, 0.15), transparent 60%), linear-gradient(180deg, rgba(5,10,15,0.7), rgba(5,10,15,0.9)); }
        .hero-container { position:relative; z-index:2; max-width:1100px; padding:32px 20px; text-align:center; color:#eaf7f0; }

        .brand { font-size: clamp(36px, 8vw, 88px); font-weight:800; letter-spacing:0.02em; line-height:1.05; margin:0; opacity:0; transform:translateY(16px) scale(0.98); animation: enterUp 700ms ease forwards; }
        .brand .glow { text-shadow: 0 0 18px rgba(34,197,94,.45), 0 0 36px rgba(34,197,94,.25); color:#34d399; }

        .tagline { margin-top:16px; font-size: clamp(14px, 2.4vw, 20px); color:#cfe9d7; opacity:0; transform:translateY(10px); animation: fadeUp 900ms 180ms ease forwards; }

        .pill-row { display:flex; gap:12px; justify-content:center; margin-top:24px; opacity:0; transform:translateY(10px); animation: fadeUp 900ms 360ms ease forwards; }
        .pill { display:inline-flex; align-items:center; gap:10px; padding:10px 14px; border:1px solid rgba(52,211,153,.45); color:#dff7ea; border-radius:999px; backdrop-filter:blur(6px); background:rgba(11,15,19,.35); box-shadow:0 2px 12px rgba(0,0,0,.35), inset 0 0 12px rgba(34,197,94,.08); }
        .dot { width:8px; height:8px; border-radius:999px; background:#34d399; box-shadow:0 0 8px rgba(34,197,94,.9); }

        .cycle { position:relative; display:inline-block; min-width:7.5ch; }
        .cycle > span { position:absolute; left:0; right:0; opacity:0; transform:translateY(14px) scale(.98); }
        .cycle > span.active { animation: cycleIn 520ms ease forwards; }
        .cycle > span.out { animation: cycleOut 380ms ease forwards; }

        @keyframes enterUp { to { opacity:1; transform:translateY(0) scale(1);} }
        @keyframes fadeUp { to { opacity:1; transform:translateY(0);} }
        @keyframes cycleIn { from { opacity:0; transform:translateY(14px) scale(.98);} to {opacity:1; transform:translateY(0) scale(1);} }
        @keyframes cycleOut { from { opacity:1; transform:translateY(0) scale(1);} to {opacity:0; transform:translateY(-12px) scale(1);} }

        .cta { margin-top:34px; display:inline-flex; align-items:center; gap:10px; border:1px solid rgba(52,211,153,.55); color:#0b0f13; background:#34d399; padding:10px 18px; border-radius:999px; font-weight:700; letter-spacing:.02em; box-shadow:0 6px 20px rgba(52,211,153,.35); transition: transform .18s ease, box-shadow .18s ease, filter .18s ease;}
        .cta:hover { transform:translateY(-1px); box-shadow:0 10px 26px rgba(52,211,153,.42); filter:saturate(1.1); }
        .cta .bolt { width:14px; height:14px; border:2px solid #0b0f13; border-left-color:transparent; border-top-color:transparent; transform:rotate(45deg); background:#0b0f13; }

        .hero-bg::before { content:""; position:absolute; inset:0; background:url('${bgUrl}') center/cover no-repeat; opacity:.9; }
      `}</style>

      <div className="hero-bg" />
      <div className="hero-overlay" />

      <div className="hero-container">
        <h1 className="brand">
          <span className="glow">EV STATION</span>
        </h1>

        <p className="tagline">
          Station:{" "}
          <span className="cycle" aria-live="polite">
            {plugs.map((p, i) => (
              <span
                key={p}
                className={i === idx ? "active" : "out"}
                style={{ position: i === idx ? "relative" : undefined }}
              >
                {p}
              </span>
            ))}
          </span>
        </p>

        <div className="pill-row" role="list" aria-label="Supported connectors">
          <span className="pill" role="listitem">
            <span className="dot" />
            CCS
          </span>
          <span className="pill" role="listitem">
            <span className="dot" />
            CHAdeMO
          </span>
          <span className="pill" role="listitem">
            <span className="dot" />
            AC Type 2
          </span>
        </div>

       
      </div>
    </section>
      
      
      </div>
    </>
  );
}
