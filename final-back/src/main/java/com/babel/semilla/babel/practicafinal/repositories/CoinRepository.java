package com.babel.semilla.babel.practicafinal.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.babel.semilla.babel.practicafinal.entities.Coin;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Integer> {
	Optional<Coin> findByName(String name);
	Optional<Coin> findBySymbol(String symbol);
}
