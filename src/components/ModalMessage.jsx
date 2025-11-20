import React, { useState } from "react";
import { Button, Modal } from "antd";
import BookingPage from "../pages/BookingPage";
const ModalMessage = () => {
  const [open, setOpen] = useState(false);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [ setModalText] = useState("");
  const showModal = () => setOpen(true);

  const handleCancel = () => {
    setOpen(false);
  };

  // Hàm này được gọi khi BookingForm submit thành công
  const handleBookingSuccess = () => {
    setModalText("Quá trình đặt xe đang diễn ra ");
    setConfirmLoading(true);
    setTimeout(() => {
      setConfirmLoading(false);
      setOpen(false);
    }, 1000);
  };

  return (
    <div className="flex justify-center mt-10">
      <Button
        type="primary"
        onClick={showModal}
        style={{
          padding: "25px 32px",
          fontSize: "20px",
          borderRadius: "10px",
        }}
      >
        Booking
      </Button>
      <Modal
        title="Reservation"
        open={open}
        confirmLoading={confirmLoading}
        footer={null}
        onCancel={handleCancel}
      >
          <BookingPage onSuccess={handleBookingSuccess} />
      </Modal>
    </div>
  );
};
export default ModalMessage;
