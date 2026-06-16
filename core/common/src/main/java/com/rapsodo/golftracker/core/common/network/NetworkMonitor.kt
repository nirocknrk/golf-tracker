package com.rapsodo.golftracker.core.common.network

// NetworkMonitor was removed — it had no implementation, no Hilt binding,
// and was never consumed by any ViewModel or use case.
//
// Re-add when offline-aware UX (e.g. a "No connection" banner) is needed:
//
//   interface NetworkMonitor {
//       val isOnline: Flow<Boolean>
//   }
//
// Then add a ConnectivityManagerNetworkMonitor impl in :data and bind via @Binds.
