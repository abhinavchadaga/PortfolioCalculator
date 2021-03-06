package com.achadaga.portfoliocalculator.entities;

import static com.achadaga.portfoliocalculator.app.Constants.LINE;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TransactionLog implements Iterable<Transaction> {

  // Internal Storage container
  private final Set<Transaction> log;

  /**
   * Construct an empty transactionLog
   */
  public TransactionLog() {
    log = new TreeSet<>();
  }

  /**
   * Construct a new transaction log from an existing TreeSet
   *
   * @param ts TreeSet from which to construct a new transaction log
   */
  public TransactionLog(Set<Transaction> ts) {
    log = new TreeSet<>(ts);
  }

  /**
   * Adds a new transaction in sorted order
   *
   * @param t Transaction to add
   */
  public void addTransaction(Transaction t) {
    log.add(t);
  }

  /**
   * Remove multiple transactions by a list of ids
   *
   * @param ids of transactions to remove
   * @return a list of the removed transactions
   */
  public TransactionLog removeTransactionsByID(List<UUID> ids, Portfolio p) {
    Set<Transaction> result = new TreeSet<>();
    for (UUID id : ids) {
      Transaction t;
      t = removeTransactionByID(id, p);
      if (t != null) {
        result.add(t);
      }
    }
    return new TransactionLog(result);
  }

  /**
   * Remove a single transaction by id
   *
   * @param id id of transaction to remove
   * @return the Transaction removed or null if no transactions exist with that id
   */
  private Transaction removeTransactionByID(UUID id, Portfolio p) {
    for (Transaction t : this) {
      if (t.getTransactionID().equals(id)) {
        log.remove(t);
        Position pos = p.findPosition(t.getTicker());
        if (pos != null) {
          pos.removeTransaction(t);
        }
        return t;
      }
    }
    return null;
  }

  /**
   * @return the total number of transactions made by the user
   */
  public int size() {
    return log.size();
  }

  /**
   * @param start the starting date
   * @param end   the ending date
   * @return a list of all Transactions that were made in this date range
   */
  public TransactionLog searchByDate(LocalDate start, LocalDate end) {
    Set<Transaction> results = new TreeSet<>();
    for (Transaction t : log) {
      if (t.getDate().compareTo(start) >= 0 && t.getDate().compareTo(end) <= 0) {
        results.add(t);
      }
    }
    return new TransactionLog(results);
  }

  /**
   * @param tgt single date to list all transactions for
   * @return a treeSet of all Transactions that were made in this date range
   */
  public TransactionLog searchByDate(LocalDate tgt) {
    return searchByDate(tgt, tgt);
  }

  /**
   * @param ticker the target ticker to list all transactions for
   * @return a list of all transactions of this stock
   */
  public TransactionLog searchByTicker(String ticker) {
    Set<Transaction> results = new TreeSet<>();
    for (Transaction t : log) {
      if (t.getTicker().equals(ticker)) {
        results.add(t);
      }
    }
    return new TransactionLog(results);
  }

  /**
   * @return a list of all buy transactions
   */
  public TransactionLog buys() {
    Set<Transaction> results = new TreeSet<>();
    for (Transaction t : log) {
      if (t instanceof Buy) {
        results.add(t);
      }
    }
    return new TransactionLog(results);
  }

  /**
   * @return a list of all sale transactions
   */
  public TransactionLog sells() {
    TreeSet<Transaction> results = new TreeSet<>();
    for (Transaction t : log) {
      if (t instanceof Sell) {
        results.add(t);
      }
    }
    return new TransactionLog(results);
  }

  @Override
  public String toString() {
    return "Transaction History: \n" + printSubLog();
  }

  public String printSubLog() {
    StringBuilder output = new StringBuilder();
    int i = 0;
    int n = log.size() - 1;
    Iterator<Transaction> it = log.iterator();
    while (i < n) {
      Transaction t = it.next();
      output.append(t).append(LINE);
      i++;
    }
    if (it.hasNext()) {
      output.append(it.next());
    }
    output.append("\ntotal number of transactions: ").append(size());
    return output.toString();
  }

  @Override
  public Iterator<Transaction> iterator() {
    return log.iterator();
  }

  @Override
  public boolean equals(Object obj) {
    // check obj class
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    TransactionLog other = (TransactionLog) obj;
    // check size
    if (this.size() != other.size()) {
      return false;
    }
    // check each entry
    Iterator<Transaction> thisIt = this.iterator();
    Iterator<Transaction> otherIt = other.iterator();
    while (thisIt.hasNext() && otherIt.hasNext()) {
      if (!thisIt.next().equals(otherIt.next())) {
        return false;
      }
    }
    return true;
  }
}
