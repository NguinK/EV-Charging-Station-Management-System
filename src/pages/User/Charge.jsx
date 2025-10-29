import React from "react";
import ModalMessage from "../../components/ModalMessage";
import { Card } from "antd";

function Charge() {
  return (
    <>
      <div className="w-8 flex justify-center ">
        <Card
          title="Booking"
          size="default"
          className="w-[500px] h-[400px]  outline-none focus:outline-none"
          tabIndex={-1}
        >
          <div className="flex flex-col items-center gap-10 select-none">
            <img
              src="images22.png"
              alt="sleep icon"
              className="w-28 h-auto pointer-events-none mb-2"
              draggable="false"
            />

            <ModalMessage />
          </div>
        </Card>
      </div>
    </>
  );
}

export default Charge;
