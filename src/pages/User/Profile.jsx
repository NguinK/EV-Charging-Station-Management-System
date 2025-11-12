import React from 'react'

function Profile() {
  return (
    <Space direction="vertical" size={20}>
    <Title level={4} className="!mb-1 !text-[#111827]">
      My Profile
    </Title>

    <Row gutter={[24, 20]}>
      {[
        { label: "Full Name", value: userInfo.fullName },
        { label: "Email", value: userInfo.email },
        { label: "Phone", value: userInfo.phone },
        { label: "Driver License", value: userInfo.driverLicense },
        { label: "Vehicle Number", value: userInfo.vehicleNumber },
        { label: "Vehicle Type", value: userInfo.vehicleType },
        { label: "Date of Birth", value: userInfo.dateOfBirth },
        { label: "Address", value: userInfo.address },
      ].map((item, i) => (
        <Col xs={24} md={12} key={i}>
          <Card
            className="bg-[#f9fafb] hover:bg-[#f3f4f6] transition-all"
            bordered={false}
          >
            <Space direction="vertical" size={8}>
              <Text className="uppercase text-[12px] text-gray-500">
                {item.label}
              </Text>
              <Text className="text-base font-semibold text-[#111827]">
                {item.value || "—"}
              </Text>
            </Space>
          </Card>
        </Col>
      ))}
    </Row>
  </Space>
  )
}

export default Profile