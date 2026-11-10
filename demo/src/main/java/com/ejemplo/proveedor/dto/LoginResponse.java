package com.ejemplo.proveedor.dto;

import java.util.List;

public record LoginResponse(String username, List<String> roles) {}
