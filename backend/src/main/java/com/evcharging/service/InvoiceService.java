package com.evcharging.service;

import com.evcharging.entity.Invoice;
import com.evcharging.entity.Transaction;
import com.evcharging.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Transactional
    public Invoice createInvoice(Transaction tx) {
        Invoice invoice = new Invoice();

        invoice.setTransaction(tx);
        invoice.setFinalAmount(tx.getAmount().doubleValue());
        invoice.setCreatedAt(OffsetDateTime.now());

        invoice.setCustomerName(tx.getDriver().getFullName());
        invoice.setStationName(tx.getSession().getStation().getName());
        invoice.setPointCode(tx.getSession().getChargingPoint().getCode());

        invoice.setInvoiceNumber("INV-" + tx.getId());

        return invoiceRepo.save(invoice);
    }

}