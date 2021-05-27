package com.qltech.common.args

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.qltech.common.extensions.getMap
import com.qltech.common.extensions.getNonNullByteArray
import com.qltech.common.extensions.getNonNullParcelable
import com.qltech.common.utils.XLog
import java.io.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance

/**
 * Because if you use a bundle to pass data between activity / fragment, there will be a problem
 * of unclear content. This will cause the developer to not know what data need to be passed in to
 * start this activity, nor do they know where these data are used.
 * So we decided to use args (data class) instead of bundle as the transport protocol.
 *
 *  Here are some examples of usage:
 *  data class YourArgs(
 *      val yourValue: String
 *  ) : BundleArgs
 *
 *  1. I need pass YourArgs form activity A to B
 *
 *      Activity A:
 *          val intent = Intent(context, A::class.java)
 *          intent.putArgs(YourArgs("your value"))
 *          context.startActivity(intent)
 *
 *      Activity B:
 *          val args by ArgsCreator<YourArgs>()
 *          override fun onCreate(savedInstanceState: Bundle?) {
 *              print(args.yourValue)
 *          }
 *
 *  2. I need reset args when receives onNewIntent
 *
 *      Activity B:
 *          val argsCreator = ArgsCreator<YourArgs>()
 *          val args by argsCreator
 *          override fun onNewIntent(intent: Intent) {
 *              super.onNewIntent(intent)
 *              setIntent(intent)
 *              args = argsCreator.createArgs(intent)
 *          }
 *
 *  3. My args will change content during the execution of activityB, and I want to restore the changed content when activity restore.
 *
 *      Activity B:
 *          va; argsCreator = ArgsCreator<YourArgs>()
 *          val args by argsCreator
 *          override fun onCreate(savedInstanceState: Bundle?) {
 *              super.onCreate(savedInstanceState)
 *              if (null != savedInstanceState) {
 *                  args = argsCreator.createArgs(savedInstanceState)
 *              }
 *          }
 *          override fun onSaveInstanceState(outState: Bundle) {
 *              super.onSaveInstanceState(outState)
 *              outState.putArgs(args)
 *          }
 */
interface BundleArgs : Serializable {

    fun toBundle(): Bundle {
        val argsKey = getClassName(this::class)

        return when (this) {
            is Parcelable -> bundleOf(argsKey to this)
            else -> bundleOf(argsKey to toByteArray())
        }.apply {
            classLoader = this@BundleArgs::class.java.classLoader
        }.also {
            XLog.d("BundleArgs", "Create $it from $this")
        }
    }

    private fun Any.toByteArray(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream)
            .use {
                it.writeObject(this)
                it.flush()
            }

        return byteArrayOutputStream.toByteArray()
    }
}

class ArgsCreator<ARGS : BundleArgs>(
    private val kClass: KClass<ARGS>,
    private val defaultArgs: (() -> ARGS)? = null
) {

    companion object {

        inline operator fun <reified ARGS : BundleArgs> invoke(noinline defaultArgs: (() -> ARGS)? = null): ArgsCreator<ARGS> {
            return ArgsCreator(ARGS::class, defaultArgs)
        }
    }

    private var value: ARGS? = null

    operator fun getValue(activity: Activity, property: KProperty<*>): ARGS {
        return try {
            value ?: createArgs(activity).apply { value = this }
        } catch (e: Exception) {
            XLog.d("BundleArgs", "${e.message}")
            createInvalidArgs(e)
        }
    }

    operator fun getValue(fragment: Fragment, property: KProperty<*>): ARGS {
        return try {
            return value ?: createArgs(fragment).apply { value = this }
        } catch (e: Exception) {
            XLog.d("BundleArgs", "${e.message}")
            createInvalidArgs(e)
        }
    }

    operator fun setValue(activity: Activity, property: KProperty<*>, args: ARGS?) {
        value = args
    }

    operator fun setValue(fragment: Fragment, property: KProperty<*>, args: ARGS?) {
        value = args
    }

    fun createArgs(intent: Intent): ARGS {
        return fromBundle(intent.extras)
    }

    fun createArgs(bundle: Bundle): ARGS {
        return fromBundle(bundle)
    }

    private fun createArgs(activity: Activity): ARGS {
        return fromBundle(activity.intent.extras)
    }

    private fun createArgs(fragment: Fragment): ARGS {
        return fromBundle(fragment.arguments)
    }

    @Suppress("UNCHECKED_CAST")
    private fun fromBundle(bundle: Bundle?): ARGS {
        return try {
            if (null == bundle) {
                throw IllegalArgumentException("bundle should not be empty")
            }

            val argsKey = getClassName(kClass)
            val bundleMap = bundle.getMap()
            XLog.d("BundleArgs", "Try to create $argsKey from bundle$bundleMap")

            if (Parcelable::class.java.isAssignableFrom(kClass.java)) {
                bundle.getNonNullParcelable<Parcelable>(argsKey) as ARGS
            } else {
                bundle.getNonNullByteArray(argsKey).toObject()
            }.also {
                XLog.d("BundleArgs", "Create $it")
            }
        } catch (e: Exception) {
            defaultArgs?.invoke() ?: throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> ByteArray.toObject(): T {
        val byteArrayInputStream = ByteArrayInputStream(this)

        return ObjectInputStream(byteArrayInputStream)
            .use {
                it.readObject() as T
            }
    }

    private fun createInvalidArgs(sourceError: Exception): ARGS {
        return try {
            kClass.createInstance()
        } catch (e: IllegalArgumentException) {
            throw sourceError
        }
    }
}

private fun getClassName(kClass: KClass<*>): String = kClass.java.name

fun Intent.putArgs(args: BundleArgs) {
    putExtras(args.toBundle())
}

fun Fragment.putArgs(args: BundleArgs) {
    with(arguments) {
        if (null == this) {
            arguments = args.toBundle()
        } else {
            putArgs(args)
        }
    }
}

fun Bundle.putArgs(args: BundleArgs) {
    putAll(args.toBundle())
}