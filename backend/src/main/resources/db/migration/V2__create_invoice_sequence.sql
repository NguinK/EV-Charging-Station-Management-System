CREATE TABLE invoice_sequence (
                                  id BIGINT IDENTITY(1000,1) PRIMARY KEY,
                                  created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);