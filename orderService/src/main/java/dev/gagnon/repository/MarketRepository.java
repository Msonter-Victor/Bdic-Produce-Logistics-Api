/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.model.Market
 *  dev.gagnon.repository.MarketRepository
 *  org.springframework.data.jpa.repository.JpaRepository
 */
package dev.gagnon.repository;

import dev.gagnon.model.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository
extends JpaRepository<Market, Long> {
}

