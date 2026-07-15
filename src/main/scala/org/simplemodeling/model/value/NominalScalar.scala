package org.simplemodeling.model.value

/**
 * Runtime contract for a domain-specific scalar whose external representation
 * is its single underlying value. Generated nominal DATATYPE classes implement
 * this marker so generic boundaries do not mistake them for structured records.
 */
trait NominalScalar extends ValueObject {
  def value: Any
}
