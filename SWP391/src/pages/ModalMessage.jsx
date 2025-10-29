import React, { useState } from "react";
import { Button, Modal } from "antd";
import BookingPage from "./BookingPage";
const ModalMessage = () => {
  const [open, setOpen] = useState(false);
  const [confirmLoading, setConfirmLoading] = useState(false);

  const showModal = () => setOpen(true);

  const handleCancel = () => {
    setOpen(false);
  };

  // Hàm này được gọi khi BookingForm submit thành công
  const handleBookingSuccess = () => {
    setConfirmLoading(true);
    setTimeout(() => {
      setConfirmLoading(false);
      setOpen(false);
    }, 1000);
  };

  return (
    <>
      <Button
        type="primary"
        onClick={showModal}
        style={{ maxWidth: 500, margin: "0 auto", marginTop: 400 }}
        className="h-12 w-full rounded-full border-none bg-gradient-to-r from-[#2cd06d] to-[#49eb85] text-base font-semibold text-[#fcfcfc] shadow-[0_15px_30px_rgba(18,70,38,0.35)] transition hover:from-[#34c759] hover:to-[#45e47d]"
      >
        Booking
      </Button>
      <Modal
        title="Booking Charging"
        open={open}
        confirmLoading={confirmLoading}
        footer={null} 
        onCancel={handleCancel}
      >
        <BookingPage onSuccess={handleBookingSuccess} />
      </Modal>
    </>
  );
};
export default ModalMessage;
