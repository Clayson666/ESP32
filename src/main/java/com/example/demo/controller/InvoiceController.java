package com.example.demo.controller;

import com.example.demo.model.Invoice;
import com.example.demo.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final PdfService pdfService;

    public InvoiceController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping(value = "/pdf", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> createInvoicePdf(@RequestBody Invoice invoice) {
        byte[] pdf = pdfService.generateInvoicePdf(invoice);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
