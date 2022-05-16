package com.template.repositories

import javax.persistence.EntityManager
import javax.persistence.Query
import kotlin.reflect.KClass

/**
 * Construct and return a parametrized [Query] from the query String [baseQueryString].
 * [baseQueryString] is extended for not null values in [queryParams] using predicates in [paramToPredicateMap].
 * @param queryParams [Map] of parameter name and value pairs
 * @param paramToPredicateMap [Map] of parameter name and predicate pairs used to extend [baseQueryString]
 * @param baseQueryString base query [String] to construct parametrized query from
 * @return parametrized [Query] result list
 */
fun EntityManager.getParametrizedQuery(
    queryParams: Map<String, Any?>,
    paramToPredicateMap: Map<String, String>,
    baseQueryString: String,
    sortColumnAndOrder: String? = ""
): Query {

    val notNullParams = queryParams.filterValues { it != null }

    val queryStr = baseQueryString +
            notNullParams.map { paramToPredicateMap[it.key] }.joinToString(" ") +
            (if (sortColumnAndOrder != "") " ORDER BY $sortColumnAndOrder" else "")

    println(queryStr)
    val query = this.createQuery(queryStr)

    queryParams.forEach {
        if (queryStr.contains(":${it.key}")) {
            query.setParameter(it.key, it.value)
        }
    }

    return query
}

/**
 * Map request's search term name with given operation (type cast is optional for casting targeted "aliasWithFieldName")
 * @param aliasWithFieldName full field name consist with table name alias and column.
 * @param operation operation for performing criteria.
 * @param castType refer to "JPA - Basic Persistable Types" (https://www.logicbig.com/tutorials/java-ee-tutorial/jpa/persistable-basic-types.html).
 *  boolean/java. lang. Boolean	BOOLEAN
 *  byte/java. lang. Byte	SMALLINT (5)
 *  short/java. lang. Short	SMALLINT (5)
 *  char/java. lang. Character	CHARACTER (n)
 *  int/java. lang. Integer	INTEGER (10)
 *  long/java. lang. Long	BIGINT (19)
 *  float/java. lang. Float	≅ DECIMAL (16,7)
 *  double/java. lang. Double	≅ DECIMAL (32,15)
 *
 * @return pair of request's search term name and predicated statement.
 * */
fun String.mapToPredicate(aliasWithFieldName: String, operation: Operation, castType: KClass<*>? = null): Pair<String, String> {

    val namedParam = when (castType)  {
            Boolean::class, Character::class, Integer::class, Long::class, Float::class, Double::class -> "CAST(:${this@mapToPredicate} AS ${castType.java.simpleName})"
            else -> ":${this@mapToPredicate}"
        }

    return this@mapToPredicate to when (operation) {
        Operation.Equal -> " AND ($aliasWithFieldName = $namedParam) "
        Operation.Like -> " AND ($aliasWithFieldName LIKE CONCAT('%%', $namedParam, '%%')) "
        Operation.GreaterThan -> " AND ($aliasWithFieldName > $namedParam) "
        Operation.LessThan -> " AND ($aliasWithFieldName < $namedParam) "
        Operation.GreaterThanOrEqual -> " AND ($aliasWithFieldName >= $namedParam) "
        Operation.LessThanOrEqual -> " AND ($aliasWithFieldName <= $namedParam) "
        Operation.In -> " AND ($aliasWithFieldName IN ($namedParam)) "
        else -> throw IllegalArgumentException("Unknown Operation \"$operation\" for mapping common predicate")
    }

}


/**
 * Map request's search term name with given operation (type cast is optional for casting targeted "aliasWithFieldName")
 * for string case-insensitive where-clause
 * @param aliasWithFieldName full field name consist with table name alias and column.
 * @param operation operation for performing criteria.
 * @param queryParams mutable map of query parameter that will use to replace value with lower case for case-insensitive comparison.
 * @return pair of request's search term name and predicated statement.
 * */
fun String.mapToIgnoreCasePredicate(aliasWithFieldName: String, operation: Operation, queryParams: MutableMap<String, Any?>): Pair<String, String> {

    val namedParam = ":${this@mapToIgnoreCasePredicate}"

    when (queryParams[this@mapToIgnoreCasePredicate])  {
        is String -> queryParams.replace(this@mapToIgnoreCasePredicate, queryParams[this@mapToIgnoreCasePredicate].toString().trim().toLowerCase())
        is List<*> -> queryParams.replace(this@mapToIgnoreCasePredicate, (queryParams[this@mapToIgnoreCasePredicate] as List<*>).map { value -> value.toString().toLowerCase() })
        null -> Unit
        else -> throw IllegalArgumentException("Neither \"kotlin.String\" nor \"kotlin.collections.List\" is a type of \"${this@mapToIgnoreCasePredicate}\"'s value but ${queryParams[this@mapToIgnoreCasePredicate]} ")
    }

    return this@mapToIgnoreCasePredicate to when (operation) {
        Operation.Equal -> " AND (LOWER($aliasWithFieldName) = $namedParam) "
        Operation.Like -> " AND (LOWER($aliasWithFieldName) LIKE CONCAT('%%', $namedParam, '%%') "
        Operation.In -> " AND (LOWER($aliasWithFieldName) IN ($namedParam)) "
        else -> throw IllegalArgumentException("Unknown Operation \"$operation\" for mapping ignore case predicate")
    }

}

/**
 * Map request's search term name with given operation (type cast is DATE for source field and casting targeted "aliasWithFieldName" with TO_DATE function with given format date)
 * @param aliasWithFieldName full field name consist with table name alias and column.
 * @param operation operation for performing criteria.
 * @param dateFormat data format (required for TO_DATE function, Default is "yyyy-MM-dd")
 *
 * @return pair of request's search term name and predicated statement.
 * */
fun String.mapToDatePredicate(aliasWithFieldName: String, dateFormat: String, operation: Operation): Pair<String, String> {

    val namedParam = ":${this@mapToDatePredicate}"

    return this@mapToDatePredicate to when (operation) {
        Operation.Equal -> " AND (DATE($aliasWithFieldName) = TO_DATE($namedParam, '$dateFormat'))) "
        Operation.GreaterThan -> " AND (DATE($aliasWithFieldName) > TO_DATE($namedParam, '$dateFormat')) "
        Operation.LessThan -> " AND (DATE($aliasWithFieldName) < TO_DATE($namedParam, '$dateFormat')) "
        Operation.GreaterThanOrEqual -> " AND (DATE($aliasWithFieldName) >= TO_DATE($namedParam, '$dateFormat')) "
        Operation.LessThanOrEqual -> " AND (DATE($aliasWithFieldName) <= TO_DATE($namedParam, '$dateFormat')) "
        else -> throw IllegalArgumentException("Unknown Operation \"$operation\" for mapping date predicate")
    }

}

/**
 * Create predicate list by column string list with single operation
 * @param alias table alias associated with column list.
 * @param operation operation for performing criteria (apply to all column).
 * @return map of request's search term name and predicated statement.
 * */
fun List<String>.mapColumnListToPredicates(alias: String, operation: Operation): Map<String, String> {
    return this@mapColumnListToPredicates.associate { it.mapToPredicate("$alias.$it", operation) }
}


/** HQL criteria filtering operators */
enum class Operation {

    /** Progress field value with "=" operation */
    Equal,

    /** Progress field value with "LIKE (Case Preserved)" operation */
    Like,

    /** Progress field value with "BETWEEN" operation */
    Between,

    /** Progress field value with ">" operation */
    GreaterThan,

    /** Progress field value with "<" operation */
    LessThan,

    /** Progress field value with ">=" operation */
    GreaterThanOrEqual,

    /** Progress field value with "<=" operation */
    LessThanOrEqual,

    /** Progress field value with "IN" operation */
    In

}

/**
 * Map RepositoryQueryParams's sort order list to single sort statement.
 * @param allowedSortFieldsWithAliases list of pair allowed sort field which available in existed entity and alias of existed entity in query.
 * @return single sort order statement
 * */
fun RepositoryQueryParams.createSortOrderStatement(allowedSortFieldsWithAliases: List<Pair<List<String>,String>>): String {

    return this@createSortOrderStatement.sortOrders.flatMap { sortOrder ->
        allowedSortFieldsWithAliases.map { allowedSortFieldsWithAlias ->
            if (!allowedSortFieldsWithAlias.first.contains(sortOrder.sortFieldName)) {
                throw IllegalArgumentException("Unknown sorted field \"${sortOrder.sortFieldName} (${sortOrder.sortOrder.name})\" by alias \"${allowedSortFieldsWithAlias.second}\"")
            } else {
                "${allowedSortFieldsWithAlias.second}.${sortOrder.sortFieldName} ${sortOrder.sortOrder.name}"
            }
        }
    }.joinToString(", ")

}