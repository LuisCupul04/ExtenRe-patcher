/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.patch

import java.util.function.Supplier

/**
 * A common interface for contexts such as [ResourcePatchContext] and [BytecodePatchContext].
 */

sealed interface PatchContext<T> : Supplier<T>
